package io.naztech.jobharvestar.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data @NoArgsConstructor @RequiredArgsConstructor
public class Proxy {

	@NonNull private String proxyIp;
	@NonNull private Integer proxyPort;

}
