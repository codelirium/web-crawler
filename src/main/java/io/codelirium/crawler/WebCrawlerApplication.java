package io.codelirium.crawler;

import io.codelirium.crawler.spider.Spider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebCrawlerApplication implements CommandLineRunner {

	public static void main(final String... args) {

		SpringApplication.run(WebCrawlerApplication.class, args);

	}

	@Override
	public void run(final String... args) throws InterruptedException {

		if (args.length != 2) {

			System.out.println("-> Please specify the url and the word to search.");


			return;
		}


		final Spider spider = new Spider();

		spider.search(args[0], args[1]);
	}
}
