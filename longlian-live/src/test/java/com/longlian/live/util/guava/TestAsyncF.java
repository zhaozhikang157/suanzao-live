package com.longlian.live.util.guava;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import com.longlian.live.util.guava.F;

/***
 * 
 * guava异步任务工具测试类
 *
 */

public class TestAsyncF {
	@Test
	public void ts() {

//		Callable<String> task = new Callable<String>() {
//			@Override
//			public String call() throws Exception {
//				return "ha! ha!";
//			}
//		};
//
//
//		F.Listenable<String> listenable = F.submit(task);

		F.Listenable<String> listenable =F.submit(()->{	return "ha! ha!";});


		// 异步结束
		F.async(listenable, new F.Callback<String>() {

			@Override
			public void onSuccess(String result) {
				System.out.println("callback execute " + result + "|||||| ");
			}

			@Override
			public void onFailure(Throwable e) {

			}

		});



		// -------------------
		F.Function<String, String> f1 = new F.Function<String, String>() {
			@Override
			public String apply(String input) {
				System.out.println("---f1 apply input " + input);
				return "f1:" + input;
			}
		};

		F.Function<String, String> f2 = new F.Function<String, String>() {
			@Override
			public String apply(String input) {
				System.out.println("---f2 apply input " + input);

				return "f2:" + input;
			}
		};
		F.Function<String, String> f3 = new F.Function<String, String>() {
			@Override
			public String apply(String input) {
				System.out.println("---f3 apply input " + input);
				return "f3:" + input;
			}
		};

		F.Listenable<String> l1 = F.immediate(f1.apply(" Tes-L1 "));
		F.Listenable<String> ll1 = F.transform(l1, f2);

		F.Listenable<String> ll2 = F.transform(ll1, f3);

		// -- 批量 执行
		F.Listenable<List<String>> fss = F.all(Arrays.asList(ll1, ll2));

		F.async(fss, new F.Callback<List<String>>() {

			@Override
			public void onFailure(Throwable arg0) {

			}

			@Override
			public void onSuccess(List<String> ls) {
				for (String key : ls) {
					System.out.println("loop --- + " + key);
				}
			}
		});

		try {
			System.out.println("ll2 ----- result: " + ll2.get());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}
