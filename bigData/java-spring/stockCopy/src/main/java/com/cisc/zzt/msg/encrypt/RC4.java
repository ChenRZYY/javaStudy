package com.cisc.zzt.msg.encrypt;

import com.google.common.base.Preconditions;

public class RC4 {
	public static final byte[] doRC4(int n, byte[] inp, String key) {
		Preconditions.checkArgument(n > 0, "Key length must be greater than 0!");
		Preconditions.checkNotNull(inp, "input is null!");
		Preconditions.checkArgument(key != null && key.length() > 0,
				"key must not null and key length must greater than 0!");
		char[] S = new char[n];
		char[] K = new char[n];
		byte[] outp = new byte[inp.length];
		int j = 0;

		int i;
		for (i = 0; i < n; ++i) {
			S[i] = (char) (i + 1);
			if (j > key.length() - 1) {
				j = 0;
			}

			K[i] = key.charAt(j);
			++j;
		}

		j = 0;

		char temp;
		for (i = 0; i < n; ++i) {
			j = (j + S[i] + K[i]) % n;
			temp = S[i];
			S[i] = S[j];
			S[j] = temp;
		}

		i = 0;
		j = 0;

		for (int x = 0; x < inp.length; ++x) {
			i = (i + 1) % n;
			j = (j + S[i]) % n;
			temp = S[i];
			S[i] = S[j];
			S[j] = temp;
			int t = (S[i] + S[j]) % n;
			int Y = S[t];
			outp[x] = (byte) (inp[x] ^ Y);
		}

		return outp;
	}
}