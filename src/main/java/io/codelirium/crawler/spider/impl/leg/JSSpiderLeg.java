package io.codelirium.crawler.spider.impl.leg;

import org.jsoup.Connection;
import org.jsoup.select.Elements;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newLinkedList;
import static java.lang.System.out;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.springframework.util.Assert.notNull;


@ThreadSafe
public class JSSpiderLeg extends BaseSpiderLeg {

	private static final Pattern JS_LIBRARY_NAME_REGEX               = Pattern.compile("/(.*?)\\.js");
	public  static final String  GOOGLE_RESULT_ANCHOR_LINK_CSS_QUERY = "h3.r > a";
	public  static final String  SCRIPT_CSS_QUERY                    = "script";
	public  static final String  SCRIPT_SRC_ATTRIBUTE_KEY            = "src";


	@Override
	public List<String> crawl(String url, final String cssQuery, final String attributeKey) {

		notNull(url, "The url cannot be null.");
		notNull(cssQuery, "The css query cannot be null.");
		notNull(attributeKey, "The attribute key cannot be null.");


		try {

			final Optional<Connection> optionalConnection = getOptionalConnection(url);

			if (!optionalConnection.isPresent()) {

				return newLinkedList();

			}


			final Elements jsLibraryStringsOnPage = optionalConnection.get().get().select(cssQuery);

			out.printf("-> Found (%d) javascript libraries.\n", jsLibraryStringsOnPage.size());


			final List<String> jsLibraries = new CopyOnWriteArrayList<>();

			jsLibraryStringsOnPage
						.parallelStream()
								.forEach(jsLibraryString -> extractJSLibraryName(jsLibraryString.absUrl(attributeKey))
																							.ifPresent(jsLibraries::add));


			return unmodifiableList(jsLibraries);

		} catch(final IOException ioe) {

			return newLinkedList();

		}
	}


	private static Optional<String> extractJSLibraryName(final String jsLibraryPath) {

		notNull(jsLibraryPath, "The javascript library path cannot be null.");


		if (jsLibraryPath.isEmpty()) {

			return empty();

		}


		final Matcher matcher = JS_LIBRARY_NAME_REGEX.matcher(jsLibraryPath);

		if (matcher.find()) {

			final String script = matcher.group();


			return of(script.substring(script.lastIndexOf("/") + 1));
		}


		return empty();
	}
}
