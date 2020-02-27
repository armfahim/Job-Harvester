package io.naztech.jobharvestar.utils;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author mahmudul.hasan
 * @since 2019-01-21
 */
@Service
@Slf4j
public class JsoupDocProvider implements ConnectionProvider {

	@Autowired
	private ProxyProvider pool;
	private static int activeIndex = 0;
	private static int poolSize;
	private static boolean degrace = false, toBeChanged = true;

	@PostConstruct
	private void setPoolSize() {
		poolSize = pool.getTotalProxyAmount();
	}

	private Document getConnection(String url) {
		if (toBeChanged) if (activeIndex == 0 && degrace || activeIndex < poolSize - 1 && !degrace) {
			degrace = activeIndex != 0;
			activeIndex++;
		} else {
			degrace = true;
			activeIndex--;
		}

		String ip = pool.getProxy(activeIndex).getProxyIp();
		try {
			Document doc = Jsoup.connect(url)
						.proxy(ip, pool.getProxy(activeIndex).getProxyPort())
						.timeout(30 * 1000).execute().parse();
			if (log.isTraceEnabled()) log.trace("Using proxy index: " + activeIndex + "; IP: " + ip);
			toBeChanged = false;
			return doc;
		} catch (IOException e) {
			if (log.isDebugEnabled()) log.debug("Failed proxy index: " + activeIndex + "; ip:" + ip, e);
			toBeChanged = true;
		}

		return null;
	}

	@Override
	public Document getConnection(String url, int maxTry) {
		Document doc = getConnection(url);
		int tries = 1;
		while (doc == null) {
			if (tries >= maxTry) break;
			doc = getConnection(url);
			tries++;
		}
		if (log.isTraceEnabled()) log.trace("Active proxy pool index : " + activeIndex);
		return doc;
	}

}
