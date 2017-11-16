package io.codelirium.crawler.spider.impl;

import io.codelirium.crawler.spider.Spider;
import io.codelirium.crawler.spider.annotation.Tarantula;
import io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static io.codelirium.crawler.spider.impl.leg.BaseSpiderLeg.urlContainsSearchTerm;
import static io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg.ANCHOR_HREF_ATTRIBUTE_KEY;
import static io.codelirium.crawler.spider.impl.leg.LinkSpiderLeg.ANCHOR_LINK_CSS_QUERY;
import static java.lang.System.out;
import static org.springframework.util.Assert.notNull;


@Tarantula
@NotThreadSafe
public class DeepLinkSpiderImpl implements Spider {

	private String       url;
	private int          depth;
	private Set<String>  pagesVisited;
	private List<String> pagesToVisit;


	public DeepLinkSpiderImpl(final String url, final int depth) {

		this.url          = url;
		this.depth        = depth;
		this.pagesVisited = newLinkedHashSet();
		this.pagesToVisit = newLinkedList();

	}


	public void search(final String searchTerm) throws IOException {

		notNull(searchTerm, "The search term cannot be null.");


		while (pagesVisited.size() < depth) {

			final String currentUrl;


			if (pagesToVisit.isEmpty()) {

				currentUrl = url;

				pagesVisited.add(url);

			} else {

				currentUrl = this.nextUrl();

			}


			if (urlContainsSearchTerm(currentUrl, searchTerm)) {

				out.printf("-> The search term [%s] was found at url: [%s].\n", searchTerm, currentUrl);


				break;
			}


			pagesToVisit.addAll(new LinkSpiderLeg().crawl(currentUrl, ANCHOR_LINK_CSS_QUERY, ANCHOR_HREF_ATTRIBUTE_KEY));
		}


		out.printf("-> Visited %d web pages.\n", pagesVisited.size());
	}


	private String nextUrl() {

		String nextUrl;

		do {

			nextUrl = pagesToVisit.remove(0);

		} while(pagesVisited.contains(nextUrl));


		pagesVisited.add(nextUrl);


		return nextUrl;
	}
}
