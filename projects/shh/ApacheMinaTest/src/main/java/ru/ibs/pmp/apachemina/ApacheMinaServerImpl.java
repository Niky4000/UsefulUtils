package ru.ibs.pmp.apachemina;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.forward.TcpipForwarder;
import org.apache.sshd.common.forward.TcpipForwarderFactory;
import org.apache.sshd.common.scp.helpers.DefaultScpFileOpener;
import org.apache.sshd.common.session.ConnectionService;
import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.GenericUtils;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import ru.ibs.pmp.apacheminatest.ApacheMina;

/**
 *
 * @author NAnishhenko
 */
@Service
public class ApacheMinaServerImpl implements ApacheMinaServer {

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${server.user.name}")
    private String userName;

    @Value("${server.user.password}")
    private String serverPassword;

    @Override
    public void createSshServer2() throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SshServer.main(new String[]{"-p", serverPort.toString(), "-io", "mina", "-key-type", "RSA"});
                } catch (Exception ex) {
                    Logger.getLogger(ApacheMinaServerImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        Thread.sleep(10000);
    }

    @Override
    public void createSshServer() throws IOException {
        if (serverPort != null && userName != null && serverPassword != null) {
            SshServer sshServer = SshServer.setUpDefaultServer();
            sshServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
            sshServer.setPasswordAuthenticator(new PasswordAuthenticator() {

                @Override
                public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException {
                    if (username.equals(userName) && password.equals(serverPassword)) {
                        return true;
                    } else {
                        return false;
                    }
                }

            });
            sshServer.setPort(serverPort);
            if (System.getProperty("os.name").contains("Linux")) {
                sshServer.setShellFactory(new ProcessShellFactory(new String[]{"/bin/sh"}));
            } else {
                sshServer.setShellFactory(new ProcessShellFactory(new String[]{"cmd.exe"}));
            }

            ScpCommandFactory scpCommandFactory = new ScpCommandFactory();
            scpCommandFactory.setDelegateCommandFactory(new CommandFactory() {
                @Override
                public Command createCommand(String command) {
                    return new ProcessShellFactory(command.split(" ")).create();
                }
            });
            scpCommandFactory.setScpFileOpener(new DefaultScpFileOpener());
//            sshServer.setCommandFactory(scpCommandFactory);

            sshServer.setCommandFactory(new ScpCommandFactory.Builder().withDelegate(new CommandFactory() {
                @Override
                public Command createCommand(String command) {
                    return new ProcessShellFactory(GenericUtils.split(command, ' ')).create();
                }
            }).build());
            sshServer.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(new SftpSubsystemFactory()));

            sshServer.setTcpipForwardingFilter(new ForwardingFilter() {
                @Override
                public boolean canForwardAgent(Session session) {
                    return true;
                }

                @Override
                public boolean canForwardX11(Session session) {
                    return true;
                }

                @Override
                public boolean canListen(SshdSocketAddress address, Session session) {
                    return true;
                }

                @Override
                public boolean canConnect(ForwardingFilter.Type type, SshdSocketAddress address, Session session) {
                    return true;
                }
            });

            try {
                System.out.println("Server starting!");
                sshServer.start();
                System.out.println("Server started!");
            } catch (IOException ex) {
                ex.printStackTrace();
                Logger.getLogger(ApacheMina.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
