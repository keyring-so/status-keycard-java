package so.keyring.keycard.desktop;

import java.io.IOException;

import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import so.keyring.keycard.globalplatform.Crypto;
import so.keyring.keycard.io.APDUCommand;
import so.keyring.keycard.io.APDUResponse;
import so.keyring.keycard.io.CardChannel;

/**
 * Implementation of a CardChannel using the Java Smartcard I/O API,
 */
public class PCSCCardChannel implements CardChannel {
  static {
    Crypto.addBouncyCastleProvider();
  }

  private javax.smartcardio.CardChannel cardChannel;

  /**
   * Constructor. Wraps a Java Smartcard I/O CardChannel.
   * @param cardChannel the card channel to wrap.
   */
  public PCSCCardChannel(javax.smartcardio.CardChannel cardChannel) {
    this.cardChannel = cardChannel;
  }

  @Override
  public APDUResponse send(APDUCommand cmd) throws IOException {
    CommandAPDU capdu = new CommandAPDU(cmd.getCla(), cmd.getIns(), cmd.getP1(), cmd.getP2(), cmd.getData(), cmd.getNeedsLE() ? 0x100 : 0x00);

    ResponseAPDU rapdu;

    try {
      rapdu = cardChannel.transmit(capdu);
    } catch (CardException e) {
      throw new IOException(e);
    }

    return new APDUResponse(rapdu.getBytes());
  }

  @Override
  public boolean isConnected() {
    return true;
  }
}
