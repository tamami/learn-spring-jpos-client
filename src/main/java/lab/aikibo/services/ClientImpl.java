package lab.aikibo.services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMUX;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISORequest;
import org.jpos.iso.channel.ASCIIChannel;
import org.jpos.iso.packager.GenericPackager;

import lab.aikibo.App;

public class ClientImpl implements Client {
  String hostname;
  int portNumber;

  private void init() {
    hostname = "localhost";
    portNumber = 15243;
  }

  @Override
  public void connect() {
    init();
    try {
      ISOPackager packager = new GenericPackager("packager/iso93ascii.xml");
      ASCIIChannel channel = new ASCIIChannel(hostname, portNumber, packager);

      ISOMUX isoMux = new ISOMUX(channel) {
        @Override
        protected String getKey(ISOMsg m) throws ISOException {
          return super.getKey(m);
        }
      };

      new Thread(isoMux).start();

      ISOMsg networkReq = new ISOMsg();
      networkReq.setMTI("1800");
      networkReq.set(3, "123456");
      networkReq.set(7, new SimpleDateFormat("yyyyMMdd").format(new Date()));
      networkReq.set(11, "000001");
      networkReq.set(12, new SimpleDateFormat("HHmmss").format(new Date()));
      networkReq.set(13, new SimpleDateFormat("MMdd").format(new Date()));
      networkReq.set(48, "Tutorial ISO 8583 Dengan Java");
      networkReq.set(70, "001");

      ISORequest req = new ISORequest(networkReq);
      isoMux.queue(req);

      ISOMsg reply = req.getResponse(50*1000);
      if(reply != null) {
        App.getLogger().debug("Req [" + new String(networkReq.pack()) + "]");
        App.getLogger().debug("Res [" + new String(reply.pack()) + "]");
      }
    } catch(ISOException isoe) {
      App.getLogger().debug(isoe);
    }
  }

  @Override
  public void connect2() {
    init();
    try {
      ISOPackager packager = new GenericPackager("packager/iso93ascii.xml");
      ASCIIChannel channel = new ASCIIChannel(hostname, portNumber, packager);

      ISOMUX isoMux = new ISOMUX(channel) {
        @Override
        protected String getKey(ISOMsg m) throws ISOException {
          return super.getKey(m);
        }
      };
      new Thread(isoMux).start();
      ISOMsg networkReq = new ISOMsg();
      networkReq.setMTI("0800");
      networkReq.set(7, new SimpleDateFormat("MMddHHmmss").format(new Date()));
      networkReq.set(11, "112233");
      networkReq.set(70, "001");

      ISORequest req = new ISORequest(networkReq);
      isoMux.queue(req);

      ISOMsg reply = req.getResponse(50*1000);
      if(reply != null) {
        App.getLogger().debug("Req [" + new String(networkReq.pack()) + "]");
        App.getLogger().debug("Res [" + new String(reply.pack()) + "]");
        App.getLogger().debug(reply.getMTI());
        App.getLogger().debug(reply.getString(1));
        App.getLogger().debug(reply.getString(2));
        App.getLogger().debug(reply.getString(7));
        App.getLogger().debug(reply.getString(11));
        App.getLogger().debug(reply.getString(39));
        App.getLogger().debug(reply.getString(70));
      }
    } catch(ISOException isoe) {
      App.getLogger().debug(isoe);
    }
  }
}
