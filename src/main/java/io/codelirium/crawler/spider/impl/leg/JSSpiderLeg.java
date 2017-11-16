package io.codelirium.crawler.spider.impl.leg;

import org.jsoup.Connection;
import org.jsoup.select.Elements;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.out;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.regex.Pattern.compile;
import static org.springframework.util.Assert.notNull;


@ThreadSafe
public class JSSpiderLeg extends BaseSpiderLeg {

	private static final Pattern JS_LIBRARY_NAME_REGEX               = compile("/(.*?)\\.js");
	public  static final String  GOOGLE_RESULT_ANCHOR_LINK_CSS_QUERY = "h3.r > a";
	public  static final String  SCRIPT_CSS_QUERY                    = "script";
	public  static final String  SCRIPT_SRC_ATTRIBUTE_KEY            = "src";

	private Connection testConnection;


	public JSSpiderLeg() {

		super();

	}

	// User for testing only.
	JSSpiderLeg(final Connection testJSSpiderLeg) {

		this.testConnection = testJSSpiderLeg;

	}


	@Override
	public List<String> crawl(String url, final String cssQuery, final String attributeKey) {

		notNull(url, "The url cannot be null.");
		notNull(cssQuery, "The css query cannot be null.");
		notNull(attributeKey, "The attribute key cannot be null.");


		try {

			final Optional<Connection> optionalConnection = Objects.isNull(this.testConnection) ? getOptionalConnection(url) : of(this.testConnection);

			if (!optionalConnection.isPresent()) {

				return emptyList();

			}


			final Elements jsLibraryPathsOnPage = optionalConnection.get().get().select(cssQuery);

			out.printf("-> Found (%d) javascript libraries.\n", jsLibraryPathsOnPage.size());


			final List<String> jsLibraries = new CopyOnWriteArrayList<>();

			jsLibraryPathsOnPage
						.parallelStream()
								.forEach(jsLibraryPath -> extractJSLibraryName(jsLibraryPath.absUrl(attributeKey))
																							.ifPresent(jsLibraries::add));


			return unmodifiableList(jsLibraries);

		} catch(final IOException ioe) {

			return emptyList();

		}
	}


	static Optional<String> extractJSLibraryName(final String jsLibraryPath) {

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
