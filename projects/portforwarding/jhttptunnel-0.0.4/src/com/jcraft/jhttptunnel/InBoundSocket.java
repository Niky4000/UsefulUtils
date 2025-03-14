/* -*-mode:java; c-basic-offset:2; -*- */
/*
Copyright (c) 2004 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jhttptunnel;

import java.net.*;
import java.io.*;

public class InBoundSocket extends InBound{
  static final private byte[] _rn="\r\n".getBytes();

  private Socket socket=null;
  private OutputStream out=null;
  private InputStream in=null;

  public void connect() throws IOException{
    close();

    String host=getHost();
    int port=getPort();
    String request="/index.html?crap=1 HTTP/1.0";
//    String request="/index.html?crap=1 HTTP/1.1";
    Proxy p=getProxy();

    if(p==null){
      socket=new Socket(host, port);
      request="GET "+request;
    }
    else{
      String phost=p.getHost();
      int pport=p.getPort();
      socket=new Socket(phost, pport);
      request="GET http://"+host+":"+port+request;
    }
    socket.setTcpNoDelay(true);

    out=socket.getOutputStream();
    out.write(request.getBytes());
    out.write(_rn);
//  if(p==null){out.write(("Connection: keep-alive").getBytes());}
//  else{out.write(("Proxy-Connection: keep-alive").getBytes());}
//  out.write(_rn);
//  out.write(("Host: "+host+":"+port).getBytes());
//  out.write(_rn);

    out.write(_rn);
    out.flush();

    in=socket.getInputStream();

    byte[] tmp=new byte[1];
    while(true){
      int i=in.read(tmp, 0, 1);
      if(i>0){
	if(tmp[0]!=0x0d){ continue; }
      }
      i=in.read(tmp, 0, 1);
      if(i>0){
	if(tmp[0]!=0x0a){ continue; }
      }
      i=in.read(tmp, 0, 1);
      if(i>0){
	if(tmp[0]!=0x0d){ continue; }
      }
      i=in.read(tmp, 0, 1);
      if(i>0){
	if(tmp[0]!=0x0a){ continue; }
      }
      break;
    }
  }

  public int receiveData(byte[] foo, int s, int l) throws IOException{
    if(l<=0){ System.out.println("receivdEdaa: "+l); }
    if(l<=0) return -1;
    while(true){
//    if(closed) return -1;
      try{
	if(foo==null){
	  if(l<=0) return -1;
	  long bar=in.skip((long)l);
	  l-=bar;
	  continue;
	}
	int i=in.read(foo, s, l);
	if(i>0){
	  return i;
	}
//System.out.println("1$ i="+i+" close="+closed);
        connect();
      }
      catch(SocketException e){
	throw e;
      }
      catch(IOException e){
System.out.println("2$ "+e);
throw e;
//        connect();
      }
    }
  }

  public void close() throws IOException{
    if(socket!=null){
      if(out!=null){ try{out.close();}catch(IOException e){} }
      if(in!=null){ try{in.close();}catch(IOException e){} }
      socket.close();
      socket=null;
    }
  }
}
