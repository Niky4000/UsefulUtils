/**
 *  (C) 2013-2014 Stephan Rauh http://www.beyondjava.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.ibs.pmp.apachemina;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.ibs.pmp.apachemina.bean.IpConfigBean;

public class Start {

    ApplicationContext applicationContext;

    public Start(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() throws Exception {
        ApacheMinaServer sshServer = applicationContext.getBean(ApacheMinaServer.class);
        ApacheMinaClient sshClient = applicationContext.getBean(ApacheMinaClient.class);
        sshServer.createSshServer();
//        sshClient.forwardL("172.29.4.26", 22, "mls", "%TGB5tgb", "127.0.0.1", 8084, 8082);
//        sshClient.forwardL("172.29.4.26", 22, "mls", "%TGB5tgb", "172.29.4.26", 8080, 8082);
//        sshClient.forwardR("172.29.4.26", 22, "mls", "%TGB5tgb", "127.0.0.1", 8084, 8080);
//        sshClient.forwardR("127.0.0.1", 8084, "hello", "world", "http://81.19.70.1/", 8082, 8086);
//        sshClient.forwardR("172.29.4.26", 22, "mls", "%TGB5tgb", "127.0.0.1", 8084, 8080);
//        sshClient.forwardR("127.0.0.1", 8084, "hello", "world", "127.0.0.1", 8080, 8082);
//
//
//
//        sshClient.forwardL("172.29.4.26", 22, "mls", "%TGB5tgb", "127.0.0.1", 8088, 8082);
//        sshClient.forwardR("172.29.4.26", 22, "mls", "%TGB5tgb", "127.0.0.1", 8084, 8088);
//        sshClient.forwardR("172.29.4.26", 22, "mls", "%TGB5tgb", "192.168.192.116", 3389, 8088);
//        sshClient.forwardR("127.0.0.1", 8084, "hello", "world", "81.19.70.1", 80, 8082);
//
//
//
        // Рабочий пример: прокинуть ресурс МГФОМСа через порт 26-го сервера!
//        sshClient.forwardL("172.29.4.26", 22, "mls", "%TGB5tgb", "127.0.0.1", 8088, 8082);
//        sshClient.forwardR("172.29.4.26", 22, "mls", "%TGB5tgb", "192.168.192.116", 3389, 8088);
        String configPath = System.getProperty("config.path");
        if (configPath != null && configPath.length() > 0) {
            List<IpConfigBean> ipConfigBeanList = new ArrayList<IpConfigBean>(configPath.length());
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(configPath + "/config.cfg")));
            for (Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                Matcher matcher = Pattern.compile("^type_(.*?)_ip_(.*?)_port_(.*?)_user_(.*?)_password_(.*?)_ip2_(.*?)_lport_(.*?)_rport_(.*?)_$", Pattern.DOTALL).matcher(value);
                if (matcher.find()) {
                    String type = matcher.group(1);
                    String ip = matcher.group(2);
                    Integer port = Integer.valueOf(matcher.group(3));
                    String user = matcher.group(4);
                    String password = matcher.group(5);
                    String ip2 = matcher.group(6);
                    Integer lport = Integer.valueOf(matcher.group(7));
                    Integer rport = Integer.valueOf(matcher.group(8));
                    ipConfigBeanList.add(new IpConfigBean(type, ip, port, user, password, ip2, lport, rport));
                }
            }
            for (IpConfigBean ipConfigBean : ipConfigBeanList) {
                if (ipConfigBean.getType().equals("R")) {
                    sshClient.forwardR(ipConfigBean.getIp(), ipConfigBean.getPort(), ipConfigBean.getUser(), ipConfigBean.getPassword(), ipConfigBean.getIp2(), ipConfigBean.getLport(), ipConfigBean.getRport());
                } else if (ipConfigBean.getType().equals("L")) {
                    sshClient.forwardL(ipConfigBean.getIp(), ipConfigBean.getPort(), ipConfigBean.getUser(), ipConfigBean.getPassword(), ipConfigBean.getIp2(), ipConfigBean.getLport(), ipConfigBean.getRport());
                }
            }
        }

        System.in.read();
    }

    public static void main(String[] args) throws Exception {
        Start start = new Start(new ClassPathXmlApplicationContext("module.xml"));
        start.start();
    }

}
