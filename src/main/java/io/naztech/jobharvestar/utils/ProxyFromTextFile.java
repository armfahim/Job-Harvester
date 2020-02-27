package io.naztech.jobharvestar.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProxyFromTextFile implements ProxyReader {

	@Override
	public List<String> getProxyStrings(String filePath) {
		List<String> proxyStrings = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClassLoader().getResourceAsStream(filePath)));
		String line;
		try {
			line = reader.readLine();
			while (line != null && line.contains(":")) {
				proxyStrings.add(line);
				line = reader.readLine();
			}
		} catch (IOException e) {
			log.error("Exception Proxy Reading from Text File");
		}

		return proxyStrings;
	}

	private ClassLoader getClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (SecurityException ex) {
		}

		if (cl == null) cl = ClassLoader.getSystemClassLoader();
		return cl;
	}

}
