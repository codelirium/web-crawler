package io.codelirium.crawler.spider.impl;

import io.codelirium.crawler.spider.Spider;
import io.codelirium.crawler.spider.annotation.Tarantula;
import io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static io.codelirium.crawler.spider.impl.leg.BaseSpiderLeg.urlContainsSearchTerm;
import static io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg.ANCHOR_HREF_ATTRIBUTE_KEY;
import static io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg.ANCHOR_LINK_CSS_QUERY;
import static java.lang.System.out;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;


@Tarantula
@ThreadSafe
public class DeepLinkSpiderImpl implements Spider {

	private String url;
	private int    depth;


	public DeepLinkSpiderImpl(final String url, final int depth) {

		this.url   = url;
		this.depth = depth;

	}


	@Override
	public void search(final String searchTerm) throws IOException {

		notNull(searchTerm, "The search term cannot be null.");


		final Set<String>  pagesVisited = newLinkedHashSet();
		final List<String> pagesToVisit = newLinkedList();

		while (pagesVisited.size() < this.depth) {

			final String currentUrl;


			if (pagesToVisit.isEmpty()) {

				currentUrl = url;

				pagesVisited.add(url);

			} else {

				currentUrl = nextUrl(pagesVisited, pagesToVisit);

			}


			if (urlContainsSearchTerm(currentUrl, searchTerm)) {

				out.printf("-> The search term [%s] was found at url: [%s].\n", searchTerm, currentUrl);


				break;
			}


			pagesToVisit.addAll(new LinkSpiderLeg().crawl(currentUrl, ANCHOR_LINK_CSS_QUERY, ANCHOR_HREF_ATTRIBUTE_KEY));
		}


		out.printf("-> Visited %d web pages.\n", pagesVisited.size());
	}


	private static String nextUrl(final Set<String> pagesVisited, final List<String> pagesToVisit) {

		notNull(pagesVisited, "The pages visited cannot be null.");
		notNull(pagesToVisit, "The pages to visit cannot be null.");
		notEmpty(pagesToVisit, "The pages to visit cannot be empty.");


		String nextUrl;

		do {

			nextUrl = pagesToVisit.remove(0);

		} while(pagesVisited.contains(nextUrl));


		pagesVisited.add(nextUrl);


		return nextUrl;
	}
}
