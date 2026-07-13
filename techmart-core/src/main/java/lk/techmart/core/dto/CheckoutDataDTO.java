package lk.techmart.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutDataDTO implements Serializable {
    private  AddressDTO addressDTO;
    public List<CartItemDTO> cartItemDTOList;
}
