package com.angke.game.io.transport.http;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.angke.game.io.transport.websocket.WebSocketServerInitializer;
import com.sun.glass.ui.TouchInputSupport;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;


/**
 * 处理 HTTP 请求
 * 扩展{@link ChannelInboundHandlerAdapter}用于处理  {@link FullHttpRequest}消息:
 * <p>
 * <ul>
 * <li>{@link HttpRequestHandler#channelRead}如果请求是WebSocket升级,
 * 递增引用计数器（保留）并且将它传递给在 {@link ChannelPipeline}中的下个{@link ChannelInboundHandler}
 * <li>{@link HttpRequestHandler#send100Continue}处理符合 HTTP 1.1的 "100 Continue" 请求,
 * <li>读取默认的 WebsocketChatClient.html 页面
 * <li>判断 keepalive 是否在请求头里面
 * <li>写 {@link HttpResponse}客户端
 * <li>写 index.html 到客户端，判断 SslHandler 是否在 {@link ChannelPipeline} 来决定是使用 DefaultFileRegion 还是 ChunkedNioFile
 * <li>写并刷新 LastHttpContent 到客户端，标记响应完成
 * <li>如果 keepalive 没有要求，当写完成时，关闭{@link ChannelHttpRequestHandler} 做了下面几件事：
 * <ul>
 * <li>如果该 HTTP请求被发送到URI “/ws”,调用 FullHttpRequest 上的 retain(),
 * 并通过调用 {@link ChannelHandlerContext#fireChannelRead} 转发到下一个
 * {@link ChannelInboundHandler.retain()} 是必要的,因为 channelRead()
 * 完成后，它会调用{@link FullHttpRequest}上的 release()来释放其资源。
 * <li>如果客户端发送的 HTTP 1.1 头是“Expect: 100-continue”,将发送“100 Continue”的响应。
 * <li>在 头被设置后，写一个 {@link HttpResponse}返回给客户端。注意，这是不是{@link FullHttpResponse},
 * 唯一的反应的第一部分。此外，我们不使用 writeAndFlush() 在这里 - 这个是在最后完成。
 * <li>如果没有加密也不压缩，要达到最大的效率可以是通过存储 index.html 的内容在一个 DefaultFileRegion 实现。
 * 这将利用零拷贝来执行传输。出于这个原因，我们检查，看看是否有一个 {@link SslHandler} 在{@link ChannelPipeline}中。
 * 另外，我们使用 ChunkedNioFile。
 * <li>写 LastHttpContent 来标记响应的结束，并终止它
 * <li>如果不要求 keepalive ，添加 {@link ChannelFutureListener}到{@link ChannelFuture}对象的最后写入,
 * 并关闭连接。注意，这里我们调用 writeAndFlush() 来刷新所有以前写的信息。
 * </ul>
 * </ul>
 * </p>
 * @author LiangShengxian
 *
 */
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {

	private final String defaultUri;
    private static final File INDEX;
    private final String appContent;
    
    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
        }
    }
    
    public HttpRequestHandler(String defaultUri, String appContent) {
        this.defaultUri = defaultUri;
        this.appContent = appContent;
    }
    
    private boolean cheackUriSuffixIsDefaultReq(String uri) {
    	boolean result = false;
    	if (uri == null || uri.isEmpty()) {
			return result;
		}
    	if (uri.equalsIgnoreCase(defaultUri)) {
    		result = true;
    	}
    	return result;
    }
    
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	if (msg instanceof FullHttpRequest) {
    		FullHttpRequest request = (FullHttpRequest) msg;
    		URI uri = new URI(request.uri());  
    		
    		System.err.println("request uri==" + uri.getPath()); 
    		if (uri.getPath().equals("/favicon.ico")) {  
    			return;  
    		}
    		
    		if (uri.getPath().startsWith("/" + appContent + "/")) {
				System.err.println("访问非公共资源");
				ctx.fireChannelRead(request);
			} else {
				if (HttpUtil.is100ContinueExpected(request)) {
					send100Continue(ctx);
				}
				
//    		if (request.method() == HttpMethod.GET) {
//    			RequestParser requestParser = new RequestParser(request);
//    			Map<String, String> parse = requestParser.parse();
//    			String string = parse.get("id");
//    			if (string != null && string.equals("ws")) {
//    				HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
//    				response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
////                        writeResponse
//    				ctx.write(response);
//    				
//    			}
//    		}
				RandomAccessFile file = null;
				if (uri.getPath().equals("/") || cheackUriSuffixIsDefaultReq(request.uri())) {
					
					//读取本地的网页厕所的客户端
					System.err.println("读取本地网页");
					System.err.println(INDEX.exists());
					System.err.println(INDEX.getName());
					file = new RandomAccessFile(INDEX, "r");
					System.err.println(file.length());
				} else {
					URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
					try {
						String path = location.toURI() + uri.getPath().substring(1);
						System.err.println("path : -->\n" + path + "; \npath.substring(5) : -->\n" + path.substring(5));
						path = !path.contains("file:") ? path : path.substring(5);
						File locationFile = new File(path);
						System.err.println(locationFile.exists());
						System.err.println(locationFile.getName());
						//读取本地的wenjian
						System.err.println("读取本地资源");
						file = new RandomAccessFile(locationFile, "r");
						System.err.println(file.length());
					} catch (URISyntaxException e) {
						throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
					}
				}
				//读取本地的网页厕所的客户端
//    			RandomAccessFile file = new RandomAccessFile(INDEX, "r");
				
				//构造一个http响应消息
				HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
				//给响应消息设置头
				if (uri.getPath().endsWith(".js")) {
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/javascript; charset=UTF-8");
				} else if (uri.getPath().endsWith(".css")) {
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/css; charset=UTF-8");
				} else {
					response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
				}
				
				//继续设置头:如果请求了keep-alive,则添加所需要的http头信息
				boolean keepAlive = HttpUtil.isKeepAlive(request);
				if (keepAlive) {
					//这里告诉客户端我们要传给他的网页数据的长度
					response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
					response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				}
				//先写给浏览器的访问端
				ctx.write(response);
				
				//把本地啊测试的客户端网页也写回去
				if (ctx.pipeline().get(SslHandler.class) == null) {
					ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
				} else {
					//这是写大文件的时候实现内存零拷贝的高效率写法噶。用这种方式,哪怕写一个G的文件也没有了撑爆JVM的顾虑
					ctx.write(new ChunkedNioFile(file.getChannel()));
				}
				//把尾的content写过去,这里需要推送出去
				ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
				if (!keepAlive) { //如果没有请求keep-alive,那么写并冲刷完成后就关闭这个连接了
					future.addListener(ChannelFutureListener.CLOSE);
				}
				file.close();
			}
		} else {
			ctx.fireChannelRead(msg);
		}
    }
    
    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

}
