package ru.ibs.pmp.apachemina.bean;

/**
 *
 * @author NAnishhenko
 */
public class IpConfigBean {

    private final String type;
    private final String ip;
    private final Integer port;
    private final String user;
    private final String password;
    private final String ip2;
    private final Integer lport;
    private final Integer rport;

    public IpConfigBean(String type, String ip, Integer port, String user, String password, String ip2, Integer lport, Integer rport) {
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
        this.ip2 = ip2;
        this.lport = lport;
        this.rport = rport;
    }

    public String getType() {
        return type;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getIp2() {
        return ip2;
    }

    public Integer getLport() {
        return lport;
    }

    public Integer getRport() {
        return rport;
    }

}
