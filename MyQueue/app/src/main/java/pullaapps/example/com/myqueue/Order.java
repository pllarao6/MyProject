package pullaapps.example.com.myqueue;

/**
 * Created by vankayap on 4/22/2015.
 */
public class Order {

    int orderid;
    String time;
    String date;
    int pay_status;
    int process_status;
    int _total;

    public Order()
    {

    }
    public Order(int orderid,String date,String time)
    {
        this.orderid=orderid;
        this.date=date;
        this.time=time;
        this.pay_status=pay_status;
        this.process_status=process_status;
    }

    public void setOrderid(int orderid)
    {
        this.orderid=orderid;
    }

    public void setDate(String date)
    {
        this.date=date;
    }

    public void setTime(String time)
    {
        this.time=time;
    }

    public int getOrderid()
    {
        return this.orderid;
    }

    public String getDate()
    {
        return this.date;
    }

    public String getTime()
    {
        return this.time;
    }

    public void setPayStatus(int status)
    {
        this.pay_status=status;
    }

    public int getPayStatus()
    {
        return this.pay_status;
    }

    public void setProcessStatus(int status)
    {
        this.process_status=status;
    }

    public int getProcessStatus()
    {
        return this.process_status;
    }

    public void setTotal(int total)
    {
        this._total=total;
    }

    public int getTotal()
    {
        return this._total;
    }

    public String toString() {
        return this.orderid + ". " + this.date + " [$" + this.time + "]";
    }

}
