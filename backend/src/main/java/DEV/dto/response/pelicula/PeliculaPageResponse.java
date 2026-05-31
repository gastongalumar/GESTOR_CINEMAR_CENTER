package DEV.dto.response.pelicula;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeliculaPageResponse {

    private List<PeliculaResponseDTO> content = new ArrayList<>();
    private long totalElements;
    private int totalPages;
    private int number;
    private int size;
}

