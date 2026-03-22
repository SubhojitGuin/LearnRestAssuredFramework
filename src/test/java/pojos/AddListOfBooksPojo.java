package pojos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class AddListOfBooksPojo {

    List<Map<String, String>> collectionOfIsbns;
    private String userId;

}
