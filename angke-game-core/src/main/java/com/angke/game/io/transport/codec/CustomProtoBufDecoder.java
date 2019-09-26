package com.angke.game.io.transport.codec;

import java.lang.reflect.Constructor;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angke.common.config.MsgConfig;
import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class CustomProtoBufDecoder extends ByteToMessageDecoder {

	private static final Logger LOG = LoggerFactory.getLogger(CustomProtoBufDecoder.class);
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		while (in.readableBytes() > 4) { // 如果可读长度小于包头长度，退出。
            in.markReaderIndex();
            
            // 获取包头中的body长度
            byte low = in.readByte();
            byte high = in.readByte();
            short s0 = (short) (low & 0xff);
            short s1 = (short) (high & 0xff);
            s1 <<= 8;
            short length = (short) (s0 | s1);

            // 获取包头中的protobuf类型
            in.readByte();
            byte dataType = in.readByte();

            // 如果可读长度小于body长度，恢复读指针，退出。
            if (in.readableBytes() < length) {
                in.resetReaderIndex();
                return;
            }

            // 读取body
            ByteBuf bodyByteBuf = in.readBytes(length);

            byte[] array;
            int offset;

            int readableLen= bodyByteBuf.readableBytes();
            if (bodyByteBuf.hasArray()) {
                array = bodyByteBuf.array();
                offset = bodyByteBuf.arrayOffset() + bodyByteBuf.readerIndex();
            } else {
                array = new byte[readableLen];
                bodyByteBuf.getBytes(bodyByteBuf.readerIndex(), array, 0, readableLen);
                offset = 0;
            }
            
            //反序列化
            MessageLite result = decodeBody(dataType, array, offset, readableLen);
            out.add(result);
        }
		
	}

	public MessageLite decodeBody(byte dataType, byte[] array, int offset, int length) throws Exception {
		Class<Object> msgClass = MsgConfig.getMsgClassByMsgCode(dataType);
    	try {
			//获取构造函数
	        //因为是私有的构造函数，要给其访问的权限   
	        Constructor privateMsgClassConstructor = msgClass.getDeclaredConstructor();
	        //设定权限为true,使其可以访问。如果没有这个权限的话，会出异常
	        privateMsgClassConstructor.setAccessible(true);
	        MessageLite newInstance = (MessageLite) privateMsgClassConstructor.newInstance();
	        return newInstance.getParserForType().parseFrom(array, offset, length);
			
	        //return msgClass.newInstance().getParserForType().parseFrom(array, offset, length);
		} catch (Exception e) {
			LOG.debug("根据解析出的消息code{},找不到对应的消息类型",dataType);
			e.printStackTrace();
		}
    	
        return null; // or throw exception
	}
	
}
