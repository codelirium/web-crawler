package io.codelirium.crawler;

import io.codelirium.crawler.spider.impl.GoogleJSSpiderImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static java.lang.System.out;
import static org.springframework.boot.Banner.Mode.OFF;


@SpringBootApplication
public class WebCrawlerApplication implements CommandLineRunner {


	public static void main(final String... args) {

		new SpringApplicationBuilder(WebCrawlerApplication.class)
														.logStartupInfo(false)
														.bannerMode(OFF)
														.run(args);

	}

	@Override
	public void run(final String... args) throws InterruptedException {

		if (args.length != 1) {

			out.println("-> Please specify a single search term.");


			return;
		}


		new GoogleJSSpiderImpl().search(args[0]);
	}
}
