package Interface;

public interface OnlyAlphabet {
    default public boolean isOnlyAlphabet(String string) {
        return string.matches("[a-zA-Zàáảãạăắằẳẵặâấầẩẫậđèéẻẽẹêếềểễệìíỉĩịòóỏõọôốồổỗộơớờởỡợùúủũụưứừửữựỳýỷỹỵ\\s]{2,100}");
    }
}
