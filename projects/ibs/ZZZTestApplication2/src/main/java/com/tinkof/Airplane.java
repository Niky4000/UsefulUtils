package com.tinkof;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Airplane {

	public Airplane(long propellerSpeed, long cameraSpeed) {
		this.propellerSpeed = propellerSpeed;
		this.cameraSpeed = cameraSpeed;
	}
	long propellerSpeed;
	long cameraSpeed;

	Semaphore semaphore = new Semaphore(1);

	public void engineWorkProcess() {
		// Напиши код работы винта здесь
		semaphore.acquireUninterruptibly();
		Application.threadPrintln("Closed");
		semaphore.release();
		Application.threadPrintln("Opened");
	}

	public void cameraWorkProcess() {
		// Напиши код работы камеры здесь
		semaphore.acquireUninterruptibly();
		Application.threadPrintln("Photo");
		semaphore.release();
	}
}
