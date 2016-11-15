package io.codelirium.crawler.spider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;


public class Spider {

	private static final int MAX_PAGES_TO_SEARCH = 10;

	private Set<String>  pagesVisited;
	private List<String> pagesToVisit;


	public Spider() {

		pagesVisited = Sets.newLinkedHashSet();
		pagesToVisit = Lists.newLinkedList();

	}


	public void search(final String url, final String word) {

		while (pagesVisited.size() < MAX_PAGES_TO_SEARCH) {

			final SpiderLeg leg = new SpiderLeg();
			final String currentUrl;


			if (pagesToVisit.isEmpty()) {

				currentUrl = url;

				pagesVisited.add(url);

			} else {

				currentUrl = this.nextUrl();

			}


			if (leg.search(currentUrl, word)) {

				System.out.printf("-> The word [%s] was found at url: [%s].\n", word, currentUrl);


				break;
			}


			pagesToVisit.addAll(leg.getLinks());
		}


		System.out.printf("-> Visited %d web pages.\n", pagesVisited.size());
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
