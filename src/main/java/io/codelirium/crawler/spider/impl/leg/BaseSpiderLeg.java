package io.codelirium.crawler.spider.impl.leg;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.lang.System.out;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.jsoup.Jsoup.connect;
import static org.springframework.util.Assert.notNull;


public abstract class BaseSpiderLeg {

	private static final String USER_AGENT = "CR4WL3R";


	public abstract List<String> crawl(final String url, final String cssQuery, final String attributeKey);


	static Optional<Connection> getOptionalConnection(final String url) throws IOException {

		notNull(url, "The url cannot be null.");


		final Connection connection = connect(url).userAgent(USER_AGENT);

		connection.get();


		final Response response = connection.response();

		if (response.statusCode() != 200) {

			out.printf("[X] The response status from url: [%s] is: [%d]. Aborting ...\n", url, response.statusCode());


			return empty();
		}

		out.printf("-> Received web page from url: [%s].\n", url);


		final String contentType = response.contentType();

		if (isNull(contentType) || !contentType.contains("text/html")) {

			out.println("[X] The response does not contain html.");


			return empty();
		}


		return of(connection);
	}

	public static boolean urlContainsSearchTerm(final String url, final String searchTerm) throws IOException {

		notNull(url, "The url cannot be null.");
		notNull(searchTerm, "The search term cannot be null.");


		out.printf("Searching for the term: [%s].\n", searchTerm);

		final Optional<Connection> optionalConnection = getOptionalConnection(url);


		return optionalConnection.isPresent() && optionalConnection
															.get().get()
															.body()
															.text()
															.toLowerCase()
															.contains(searchTerm.toLowerCase());
	}
}
