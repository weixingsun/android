package cat.app.sensor.net.udp.nouse;

public class UDPServer1 extends UDPAgent {
	public static void main(String[] args) throws Exception {
		new UDPServer1(2008).start();
	}

	public UDPServer1(int port) {
		super(port);
	}
}

/*
 * 
 * 1。启动一个Server. 
 * 2。启动两个Client. 然后从Server端的Console里边可以看到两个Client的NAT后的地址和端口。
 * 在Server段输入命令 send a.a.a.a A send b.b.b.b B hello
 * a.a.a.a是第一个Client的NAT后的ip,A端口号。 b是第二个。。。 输入这个命令后，A就会直接发给B一个 hello。 发送成功。
 * 如果是同一个NAT后边，可能要让A发送到B的内网地址才能成功。
 */