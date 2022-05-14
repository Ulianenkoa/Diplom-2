package stellarburgers;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Order {
    public List<String> ingredients;
}
