package ru.bersa.recyclertest;

/**
 * Created by BerSA on 05.09.2019.
 */
public class ImgContainer {
    private String id;
    private String owner;
    private String secret;
    private String server;
    private int farm;
    private String title;
    private int ispublic;
    private int isfamily;
    private int is_primary;
    private int has_comment;

    public ImgContainer(String id, String owner, String secret, String server, int farm, String title, int ispublic, int isfamily, int is_primary, int has_comment) {
        this.id = id;
        this.owner = owner;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
        this.title = title;
        this.ispublic = ispublic;
        this.isfamily = isfamily;
        this.is_primary = is_primary;
        this.has_comment = has_comment;
    }

    //return "https://farm9.staticflickr.com/8187/8432423659_dd1b834ec5_[zn].jpg";
    private String getPhotoUrl(String pfx){
        return "https://farm"+farm+".staticflickr.com/"+server+"/"+id+"_"+secret+pfx+".jpg";
    }

    public String getPreview() {
       return getPhotoUrl("_n");
    }

    public String getMainPhoto() {
        return getPhotoUrl("");
    }

    public String getTitle() {
        return title;
    }


}
