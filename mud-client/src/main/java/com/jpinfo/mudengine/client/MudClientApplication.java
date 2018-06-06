package com.jpinfo.mudengine.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArraySingleTerminatorSerializer;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MudClientApplication {


	@Bean
	public TcpNetServerConnectionFactory getConnFactory() {
		
		TcpNetServerConnectionFactory factory =new TcpNetServerConnectionFactory(9876); 
		
		factory.setSerializer(new ByteArraySingleTerminatorSerializer((byte) 0));
		
		return factory;
	}
	
	@Bean
	public TcpInboundGateway tcpGateway(MudClientGateway inboundGateway) {
		
		inboundGateway.setConnectionFactory(getConnFactory());
		inboundGateway.setRequestChannelName("requestChannel");
		
		return inboundGateway;
	}
	
	
	@Bean(name="requestChannel")
	public MessageChannel getRequestChannel() {
		return new DirectChannel();
	}
	
	@Bean
	@Transformer(inputChannel = "requestChannel", outputChannel="plainRequestChannel")
	public org.springframework.integration.transformer.Transformer getRequestTransformer() {
		return new ObjectToStringTransformer();
	}
	
	
    public static void main(String[] args) throws Exception {
		
		ConfigurableApplicationContext ctx = SpringApplication.run(MudClientApplication.class, args);
		
		System.in.read();
		
		ctx.close();
	}
    
    
    
}
