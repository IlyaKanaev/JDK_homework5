package homework;
// Объекты типа "вилка" имеют только состояние, пользуются ими или нет
public class Fork {
    private boolean using; // определяет, поьзуются ли сейчас вилкой

    public boolean isUsing() {
        return using;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }
}
