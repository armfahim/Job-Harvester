package io.naztech.jobharvestar.utils;

import org.springframework.stereotype.Component;

@Component
public interface ProxyProvider {
	public Proxy getProxy(int proxyIndex);
	public int getTotalProxyAmount();
}
