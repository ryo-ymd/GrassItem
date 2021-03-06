package life.grass.grassitem;

import com.google.gson.JsonElement;

import java.util.List;
import java.util.Optional;

public class GrassJsonDataValue {

    private JsonElement jsonElement;
    private String mask;
    private List<JsonElement> enchants;

    /* package */ GrassJsonDataValue(JsonElement jsonElement, String mask, List<JsonElement> enchants) {
        this.jsonElement = jsonElement;
        this.mask = mask == null ? null : mask.replace("\"", "");
        this.enchants = enchants;
    }

    GrassJsonDataValue(JsonElement jsonElement, String mask) {
        this.jsonElement = jsonElement;
        this.mask = mask == null ? null : mask.replace("\"", "");
    }

    public Optional<String> getAsOriginalString() {
        return Optional.ofNullable(jsonElement == null ? null : jsonElement.getAsString());
    }

    public Optional<String> getAsOverwritedString() {
        return Optional.ofNullable(mask);
    }

    public Optional<String> getAsMaskedString() {
        return Optional.ofNullable(getAsOverwritedString().orElse(getAsOriginalString().orElse(null)));
    }

    public Optional<Integer> getAsOriginalInteger() {
        return Optional.of(getAsOriginalDouble().get().intValue());
    }

    public Optional<Integer> getAsOverwritedInteger() {
        return Optional.ofNullable(!getAsOverwritedDouble().isPresent() ? null : getAsOverwritedDouble().get().intValue());
    }

    public Optional<Integer> getAsMaskedInteger() {
        return Optional.of(getAsMaskedDouble().get().intValue());
    }

    public Optional<Double> getAsOriginalDouble() {
        return Optional.of(jsonElement == null ? 0 : jsonElement.getAsDouble());
    }

    public Optional<Double> getAsOverwritedDouble() {
        return Optional.ofNullable(mask == null ? null : Double.parseDouble(mask));
    }

    public Optional<Double> getAsMaskedDouble() {
        Double value = getAsOriginalDouble().get();
        Double result = mask == null || mask.equalsIgnoreCase("") ? value : calculate(value, mask);

        if(enchants != null) {
            for(JsonElement element: enchants) {
                if(element != null)
                    result = calculate(result, element.getAsString());
            }
        }

        return Optional.of(result);
    }

    private double calculate(double base, String mask) {
        String value = mask.substring(1);
        if (mask.startsWith("+")) {
            return base + Double.valueOf(value);
        } else if (mask.startsWith("-")) {
            return base - Double.valueOf(value);
        } else if (mask.startsWith("*")) {
            return base * Double.valueOf(value);
        } else if (mask.startsWith("/")) {
            return base / Double.valueOf(value);
        } else {
            try {
                return Double.valueOf(mask);
            } catch(Exception e) {
                return 0.0;
            }
        }
    }
}
