package org.qwikpe.callback.handler;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class CallbackHandlerApplication {

	public static void main(String[] args) {

		Security.addProvider(new BouncyCastleProvider());
		SpringApplication.run(CallbackHandlerApplication.class, args);
	}
}
