package io.codelirium.crawler.spider;

import com.google.common.collect.Lists;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;


public class SpiderLeg {

	private static final String USER_AGENT = "Foo Agent";

	private List<String> links;
	private Document     htmlDocument;


	public SpiderLeg() {

		links = Lists.newLinkedList();

	}


	public boolean crawl(final String url) {

		try {

			final Connection connection   = Jsoup.connect(url).userAgent(USER_AGENT);
			final Document   htmlDocument = connection.get();


			this.htmlDocument = htmlDocument;


			if (connection.response().statusCode() == 200) {

				System.out.printf("-> Received web page from url: [%s].\n", url);

			}


			if (!connection.response().contentType().contains( "text/html" )) {

				System.out.println("[X] The response does not contain html.");


				return false;
			}


			final Elements linksOnPage = htmlDocument.select( "a[href]" );

			System.out.printf("-> Found (%d) links.\n", linksOnPage.size());

			linksOnPage.forEach(link -> this.links.add(link.absUrl( "href" )));


			return true;

		} catch(final IOException ioe) {

			return false;

		}
	}


	public boolean search(final String url, final String word) {

		if (isNull(htmlDocument)) {

			crawl(url);

		}


		System.out.printf("Searching for the word: [%s].\n", word);


		return htmlDocument
						.body()
						.text()
						.toLowerCase()
								.contains(word.toLowerCase());
	}


	public List<String> getLinks() {

		return links;

	}
}
