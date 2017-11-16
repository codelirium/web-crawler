package io.codelirium.crawler.spider;

import java.io.IOException;


public interface Spider {

	void search(final String searchTerm) throws IOException;

}
