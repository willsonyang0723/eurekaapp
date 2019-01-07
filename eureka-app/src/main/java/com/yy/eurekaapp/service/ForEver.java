package com.yy.eurekaapp.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class ForEver {

	private static FileOutputStream fos;
	
	static {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				File file=new File("aaa");
				try {
					fos = new FileOutputStream(file);
					fos.write(1);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				while(true) {
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("<======== im running");
				}
			}
		}).start();;
	}
}
