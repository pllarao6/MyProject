package pullaapps.example.com.myqueue;

import java.io.Serializable;

public class Meal implements Serializable {
    //private variables
    int _id;
    String _item_name;
    int _item_quantity;
    int _item_price;
    int _item_total_price;
    int _merchant_id;
    int _customer_id;
    String _store_name;
    int _status;
    int _processed;
    // Empty constructor
    public Meal(){

    }

    public Meal(String name)
    {
        this._item_name=name;
    }
    // constructor
    public Meal(String item_name, int quantity,int item_price,int item_total_price,int merchant_id,int customer_id,String store_name){
        this._item_name = item_name;
        this._item_quantity= quantity;
        this._item_price=item_price;
        this._item_total_price=item_total_price;
        this._merchant_id=merchant_id;
        this._customer_id=customer_id;
        this._store_name=store_name;
    }


    public Meal(int id, String item_name, int quantity,int item_price,int item_total_price,int merchant_id,int customer_id){
        this._id = id;
        this._item_name = item_name;
        this._item_quantity= quantity;
        this._item_price=item_price;
        this._item_total_price=item_total_price;
        this._merchant_id=merchant_id;
        this._customer_id=customer_id;
    }

    // getting ID
    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }

    // getting name
    public String getItemName(){
        return this._item_name;
    }

    // setting name
    public void setItemName(String name){
        this._item_name = name;
    }

    public int getQuantity(){
        return this._item_quantity;
    }

    public void setQuantity(int quantity){
        this._item_quantity = quantity;
    }

    public int getItemPrice(){
        return this._item_price;
    }

    // setting phone number
    public void setItemPrice(int price){
        this._item_price = price;
    }

    public int getItemTotalPrice(){
        return this._item_total_price;
    }

    // setting phone number
    public void setItemTotalPrice(int price){
        this._item_total_price = price;
    }

    public int getMerchantId(){
        return this._merchant_id;
    }

    // setting phone number
    public void setMerchantId(int id){
        this._merchant_id = id;
    }

    public int getCustomerId(){
        return this._customer_id;
    }

    // setting phone number
    public void setCustomerId(int id){
        this._customer_id = id;
    }

    public void setStoreName(String storeName)
    {
        this._store_name=storeName;
    }
    public String getStoreName()
    {
        return this._store_name;
    }

    public void set_status(int status)
    {
        this._status=status;
    }

    public int get_status()
    {
        return this._status;
    }

    public void set_processed(int processed)
    {
        this._processed=processed;
    }

    public int get_processed()
    {
        return this._processed;
    }
}

