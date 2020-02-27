package io.naztech.jobharvestar.utils;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaticProxyProvider implements ProxyProvider {
	private List<Proxy> list;

	@Autowired
	private ProxyReader proxyReader;

	@PostConstruct
	private void init() {
		list = proxyReader.getProxyStrings("ip.txt").stream()
				.map(proxyString -> proxyString.split(":"))
				.map(parts -> new Proxy(parts[0], Integer.valueOf(parts[1].trim())))
				.collect(Collectors.toList());
	}

	@Override
	public Proxy getProxy(int index) {
		return list.get(index);
	}

	@Override
	public int getTotalProxyAmount() {
		return list.size();
	}

}
