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
 * 1������һ��Server. 
 * 2����������Client. Ȼ���Server�˵�Console��߿��Կ�������Client��NAT��ĵ�ַ�Ͷ˿ڡ�
 * ��Server���������� send a.a.a.a A send b.b.b.b B hello
 * a.a.a.a�ǵ�һ��Client��NAT���ip,A�˿ںš� b�ǵڶ��������� ������������A�ͻ�ֱ�ӷ���Bһ�� hello�� ���ͳɹ���
 * �����ͬһ��NAT��ߣ�����Ҫ��A���͵�B��������ַ���ܳɹ���
 */