package day2;

class CountryForKey {
    String[] data = new String[256];

    CountryForKey() {
        for (int i = 0; i < 256; i++) {
            data[i] = "Country_" + i;
        }
    }

    String lookup(byte key) {
        return data[Byte.toUnsignedInt(key)];
    }
}
