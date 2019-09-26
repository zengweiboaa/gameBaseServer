package com.angke.game.io.transport.codec;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angke.common.config.MsgConfig;
import com.google.protobuf.MessageLite;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomProtoBufEncoder extends MessageToByteEncoder<MessageLite> {

	private static final Logger LOG = LoggerFactory.getLogger(CustomProtoBufEncoder.class);
	
	@Override
	protected void encode(ChannelHandlerContext ctx, MessageLite msg, ByteBuf out) throws Exception {
		byte[] body = msg.toByteArray();
        byte[] header = encodeHeader(msg, (short)body.length);

        LOG.info("编码结构:");
        LOG.info("encode header:{}",Arrays.toString(header));
        LOG.info("encode body:{}",Arrays.toString(body));
        out.writeBytes(header);
        out.writeBytes(body);
        LOG.info("开始发送");
        return;
	}
	
	private byte[] encodeHeader(MessageLite msg, short bodyLength) {
        byte messageType = 0x0f;
        if (msg != null) {
        	try {
        		short msgCode = MsgConfig.getMsgCodeByMsgClass(msg.getClass());
        		messageType = (byte) msgCode;
        		//logger.debug("encode message type:" + messageType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        
        byte[] header = new byte[4];
        header[0] = (byte) (bodyLength & 0xff);
		//logger.debug("encode body length:" + bodyLength);
        header[1] = (byte) ((bodyLength >> 8) & 0xff);
        header[2] = 0; // 保留字段
        header[3] = messageType;

        return header;
    }

}
