package io.naztech.jobharvestar.utils;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface ProxyReader {
	public List<String> getProxyStrings(String filePath);
}
