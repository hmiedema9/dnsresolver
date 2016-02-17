/**
 * Created by Douglas on 2/17/2016.
 */
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.StringTokenizer;
public class DNSPacket {
    public static final short TYPE_A = 1;
    public static final short CLASS_IN = 1;

    short id;
    short flags;
    short qcount;
    short ancount;
    short authcount;
    short addcount;
    DNSQuestion question;

    public DNSPacket(){
        id=(short)(new Random()).nextInt();
        flags=0;
        qcount=1;
        ancount=0;
        authcount=0;
        addcount=0;
        question = new DNSQuestion();
    }

    public DNSPacket(String name){
        this();
        this.question.setQName(name);
        this.setRD();
    }

    public DNSPacket(byte[] packetbytes){
        //make the byte buffer
        ByteBuffer b = ByteBuffer.wrap(packetbytes);
        //read the basic header fields
        id = b.getShort();
        flags = b.getShort();
        qcount = b.getShort();
        ancount = b.getShort();
        authcount = b.getShort();
        addcount = b.getShort();
        //read the query name
        question = new DNSQuestion();
        question.setQName(readDNSName(b));
        question.setDnsType(b.getShort());
        question.setDnsClass(b.getShort());
        //should now read the resource records in a loop
        //probably make a separate class for them
        //and put them in a list or something like that
    }

    private String readDNSName(ByteBuffer b){
        //slightly untested for pointers, but looks correct
        byte labelLength = b.get();
        String result="";
        while(labelLength!=0){
            if((labelLength&0xC0)==0xC0){
                byte nextbyte = b.get();
                int target = ((labelLength&0x3F)<<8)+nextbyte;
                ByteBuffer nb = b.duplicate();
                nb.position(target);
                if(result!=""){
                    result +=".";
                }
                result += readDNSName(nb);
            }
            byte[] tmp = new byte[labelLength];
            b.get(tmp);
            String token = new String(tmp);
            if(result!=""){
                result +=".";
            }
            result += token;
            labelLength = b.get();
        }
        return result;
    }

    public byte[] getQueryBytes(){
        ByteBuffer b = ByteBuffer.allocate(512);
        b.putShort(id);
        b.putShort(flags);
        b.putShort(qcount);
        b.putShort(ancount);
        b.putShort(authcount);
        b.putShort(addcount);
        StringTokenizer st = new StringTokenizer(question.getQName(),".");
        while(st.hasMoreTokens()){
            String token = st.nextToken();
            b.put((byte)token.length());
            b.put(token.getBytes());
        }
        b.put((byte)0); //end of domain name marker
        //b.putShort(querypacket.TYPE_A);
        //b.putShort(querypacket.CLASS_IN);
        b.flip();
        byte[] ba = new byte[b.limit()];
        b.get(ba);
        return ba;
    }

    public void setRD(){
        //set the RD bit. Unlike in C, in Java this is exactly where
        //we would expect based on the documentation
        flags = (short)(flags | (1<<8));
    }

    public short getAnswerCount(){
        return ancount;
    }

    public short getRCode(){
        return (short)(flags&(short)0b0000000000001111);
    }

    public DNSQuestion getQuestion(){
        return question;
    }
}
