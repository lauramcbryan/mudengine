package com.jpinfo.mudengine.client;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArraySingleTerminatorSerializer;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpinfo.mudengine.client.model.VerbDictionary;

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
	
	@Bean
	public VerbDictionary initializeVerbDictionary() throws JsonParseException, JsonMappingException, IOException {
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		
		VerbDictionary verbDictionary = 
					jsonMapper.readValue(
							new ClassPathResource("system-verbs.json").getFile(), 
							new TypeReference<VerbDictionary>() {});
		
		return verbDictionary;
	}
	
	
	
    public static void main(String[] args) throws Exception {
		
		ConfigurableApplicationContext ctx = SpringApplication.run(MudClientApplication.class, args);
		
		System.in.read();
		
		ctx.close();
	}
    
    
    
}
