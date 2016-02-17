/**
 * Created by Douglas on 2/17/2016.
 */
public class DNSQuestion {
    String dnsname;
    short dnstype;
    short dnsclass;

    public DNSQuestion(){
        dnsname="";
        dnstype=DNSPacket.TYPE_A;
        dnsclass=DNSPacket.CLASS_IN;
    }

    public short getDnsType(){
        return dnstype;
    }

    public short getDnsClass(){
        return dnsclass;
    }

    public String getQName(){
        return dnsname;
    }

    public void setQName(String name){
        dnsname = name;
    }

    public void setDnsType(short type){
        dnstype=type;
    }

    public void setDnsClass(short clas){
        dnsclass = clas;
    }
}
