package com.angke.game.io.transport.httpfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

/**
 * 通过http文件下载
 * @author LiangShengxian
 *
 */
public class HttpFileDownLoadServerHandler extends ChannelInboundHandlerAdapter {

	private final String url;
    
    public HttpFileDownLoadServerHandler(String url) {
        this.url = url;
    }
    
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (!(msg instanceof FullHttpRequest)) {
			return;
		}
    	FullHttpRequest request = (FullHttpRequest)msg;
    	if(!request.decoderResult().isSuccess()){
    		sendError(ctx, HttpResponseStatus.BAD_REQUEST);
    		return;
    	}
    	if(request.method() != HttpMethod.GET){
    		sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
    		return;
    	}
    	
    	final String uri = request.uri();
        final String path = sanitizeUri(uri);
        if(path == null){
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        File file = new File(path);
        if(file.isHidden() || !file.exists())
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        if(file.isDirectory())
        {
            if(uri.endsWith("/"))
            {
                sendListing(ctx, file);
            }else{
                sendRedirect(ctx, uri + "/");
            }
            return;
        }
        if(!file.isFile())
        {
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        RandomAccessFile randomAccessFile = null;
        try{
            randomAccessFile = new RandomAccessFile(file, "r");
        }catch(FileNotFoundException fnfd){
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        
        long fileLength = randomAccessFile.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, fileLength);
//        setContentLength(response, fileLength);
        setContentTypeHeader(response, file);
        
        
        
        if(HttpUtil.isKeepAlive(request)){
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        
        ctx.write(response);
        ChannelFuture sendFileFuture = null;
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            
            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                    throws Exception {
                System.out.println("Transfer complete.");
                
            }
            
            @Override
            public void operationProgressed(ChannelProgressiveFuture future,
                    long progress, long total) throws Exception {
                if(total < 0)
                    System.err.println("Transfer progress: " + progress);
                else
                    System.err.println("Transfer progress: " + progress + "/" + total);
            }
        });
        
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if(!HttpUtil.isKeepAlive(request))
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
    	
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive())
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
    }
    
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private String sanitizeUri(String uri){
        try{
            uri = URLDecoder.decode(uri, "UTF-8");
        }catch(UnsupportedEncodingException e){
            try{
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            }catch(UnsupportedEncodingException e1){
                throw new Error();
            }
        }
        
        if(!uri.startsWith(url))
            return null;
        if(!uri.startsWith("/"))
            return null;
        
        uri = uri.replace('/', File.separatorChar);
        if(uri.contains(File.separator + '.') || uri.contains('.' + File.separator) || uri.startsWith(".") || uri.endsWith(".") 
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        return System.getProperty("user.dir") + File.separator + uri;
    }
    
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    
    private static void sendListing(ChannelHandlerContext ctx, File dir){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
//        response.headers().set("CONNECT_TYPE", "text/html;charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        
        String dirPath = dir.getPath();
        StringBuilder buf = new StringBuilder();
        
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("目录:");
        buf.append("</title></head><body>\r\n");
        
        buf.append("<h3>");
        buf.append(dirPath).append(" 目录：");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>链接：<a href=\" ../\")..</a></li>\r\n");
        for (File f : dir.listFiles()) {
            if(f.isHidden() || !f.canRead()) {
                continue;
            }
            String name = f.getName();
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }
            
            buf.append("<li>链接：<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        
        buf.append("</ul></body></html>\r\n");
        
        ByteBuf buffer = Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);  
        response.content().writeBytes(buffer);  
        buffer.release();  
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE); 
    }
    
    
    private static void sendRedirect(ChannelHandlerContext ctx, String newUri){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
//        response.headers().set("LOCATIN", newUri);
        response.headers().set(HttpHeaderNames.LOCATION, newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, 
                Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    private static void setContentTypeHeader(HttpResponse response, File file){
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
    }
    
    
}

