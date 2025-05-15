package dto;

public class GameDTO {
    private String gameID;
    private String gameName;
    private String developer;
    private String genre;
    private double price;
    private boolean status;
    private String image;

    public GameDTO() {
        this.gameID = "";
        this.gameName = "";
        this.developer = "";
        this.genre = "";
        this.price = 0.0;
        this.image = "";
    }

    public GameDTO(String gameID, String gameName, String developer, String genre, double price, boolean status, String image) {
        this.gameID = gameID;
        this.gameName = gameName;
        this.developer = developer;
        this.genre = genre;
        this.price = price;
        this.status = status;
        this.image = image;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
}
