package com.example.examtp;

import com.example.examtp.dto.evaluation.create.CreateEvaluationDto;
import com.example.examtp.dto.evaluation.create.CreateEvaluationMapper;
import com.example.examtp.dto.evaluation.read.EvaluationDto;
import com.example.examtp.dto.evaluation.read.EvaluationMapper;
import com.example.examtp.entities.Evaluation;
import com.example.examtp.entities.Restaurant;
import com.example.examtp.exceptions.AppException;
import com.example.examtp.repositories.EvaluationRepository;
import com.example.examtp.repositories.RestaurantRepository;
import com.example.examtp.services.evaluation.EvaluationSearchService;
import com.example.examtp.services.evaluation.EvaluationServicesImp;
import com.example.examtp.services.uploadS3.S3UploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServicesImpTest {

    @Mock private EvaluationRepository evaluationRepository;
    @Mock private EvaluationMapper evaluationMapper;
    @Mock private CreateEvaluationMapper createEvaluationMapper;
    @Mock private S3UploadService uploadService;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private EvaluationSearchService evaluationSearchService;

    @InjectMocks private EvaluationServicesImp service;

    @Captor private ArgumentCaptor<Evaluation> evaluationCaptor;

    @Test
    @DisplayName("getAllEvaluations - retourne la liste mappée de DTO")
    void getAllEvaluations_returnsDtos() {
        Evaluation entity = new Evaluation();
        EvaluationDto dto = mock(EvaluationDto.class);

        when(evaluationRepository.findAll()).thenReturn(List.of(entity));
        when(evaluationMapper.toDto(entity)).thenReturn(dto);

        List<EvaluationDto> result = service.getAllEvaluations();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isSameAs(dto);
        verify(evaluationRepository).findAll();
        verify(evaluationMapper).toDto(entity);
    }

    @Test
    @DisplayName("getEvaluationsByKeyword - délègue à EvaluationSearchService")
    void searchByKeyword_delegates() {
        when(evaluationSearchService.searchEvaluations("menu", 10)).thenReturn(List.of());

        List<EvaluationDto> result = service.getEvaluationsByKeyword("menu", 10);

        assertThat(result).isEmpty();
        verify(evaluationSearchService).searchEvaluations("menu", 10);
    }

    @Test
    @DisplayName("getMyEvaluations - auteur trouvé -> map DTOs")
    void getMyEvaluations_found() {
        Evaluation e1 = new Evaluation();
        Evaluation e2 = new Evaluation();
        EvaluationDto d1 = mock(EvaluationDto.class);
        EvaluationDto d2 = mock(EvaluationDto.class);

        when(evaluationRepository.findByAuthor("Lucien"))
                .thenReturn(Optional.of(List.of(e1, e2)));
        when(evaluationMapper.toDto(e1)).thenReturn(d1);
        when(evaluationMapper.toDto(e2)).thenReturn(d2);

        List<EvaluationDto> result = service.getMyEvaluations("Lucien");

        assertThat(result).containsExactly(d1, d2);
    }

    @Test
    @DisplayName("getMyEvaluations - aucun résultat -> AppException 404")
    void getMyEvaluations_notFound() {
        when(evaluationRepository.findByAuthor("Unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMyEvaluations("Unknown"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("No evaluations found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("createEvaluation - upload des images, persistance et indexation")
    void createEvaluation_withFiles_persists_and_indexes() {
        CreateEvaluationDto dto = mock(CreateEvaluationDto.class);
        Evaluation toSave = new Evaluation();
        toSave.setContent("Excellent !");
        toSave.setNote(3);
        Evaluation saved = new Evaluation();
        saved.setId(42L);
        saved.setContent("Excellent !");
        saved.setNote(3);

        Restaurant restaurant = new Restaurant();
        restaurant.setId(5L);
        restaurant.setName("Chez Test");

        MultipartFile f1 = mock(MultipartFile.class);
        MultipartFile f2 = mock(MultipartFile.class);

        when(createEvaluationMapper.toEntity(dto)).thenReturn(toSave);
        when(restaurantRepository.findById(anyLong())).thenReturn(Optional.of(restaurant));
        when(dto.evaluationImages()).thenReturn(List.of(f1, f2));
        when(uploadService.uploadEvaluationImage(f1)).thenReturn("url://f1");
        when(uploadService.uploadEvaluationImage(f2)).thenReturn("url://f2");
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(saved);
        when(evaluationMapper.toDto(saved)).thenReturn(mock(EvaluationDto.class));
        // simulate dto.restaurantId() used in the service
        when(dto.restaurantId()).thenReturn(5L);

        EvaluationDto result = service.createEvaluation(dto,"tester");

        assertThat(result).isNotNull();
        verify(uploadService).uploadEvaluationImage(f1);
        verify(uploadService).uploadEvaluationImage(f2);
        verify(evaluationRepository).save(evaluationCaptor.capture());
        assertThat(evaluationCaptor.getValue().getEvaluationImagesUrls()).containsExactly("url://f1", "url://f2");
    }

    @Test
    @DisplayName("createEvaluation - restaurant introuvable -> AppException 404")
    void createEvaluation_restaurantNotFound() {
        CreateEvaluationDto dto = mock(CreateEvaluationDto.class);
        when(createEvaluationMapper.toEntity(dto)).thenReturn(new Evaluation());
        when(dto.restaurantId()).thenReturn(123L);
        when(restaurantRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createEvaluation(dto, "tester"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Restaurant not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteEvaluation - existant -> supprime et désindexe")
    void deleteEvaluation_exists() {
        when(evaluationRepository.existsById(9L)).thenReturn(true);

        service.deleteEvaluation(9L);

        verify(evaluationRepository).deleteById(9L);
        verify(evaluationSearchService).deleteEvaluation(9L);
    }

    @Test
    @DisplayName("deleteEvaluation - inexistant -> AppException 404")
    void deleteEvaluation_notExists() {
        when(evaluationRepository.existsById(9L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteEvaluation(9L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Evaluation not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteMyEvaluation - trouvé par auteur et id -> supprime et désindexe")
    void deleteMyEvaluation_found() {
        Evaluation e = new Evaluation();
        e.setId(7L);
        when(evaluationRepository.findByAuthorAndId("Lucien", 7L)).thenReturn(Optional.of(e));

        service.deleteMyEvaluation(7L, "Lucien");

        verify(evaluationRepository).delete(e);
        verify(evaluationSearchService).deleteEvaluation(7L);
    }

    @Test
    @DisplayName("deleteMyEvaluation - introuvable -> AppException 404")
    void deleteMyEvaluation_notFound() {
        when(evaluationRepository.findByAuthorAndId("Lucien", 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteMyEvaluation(7L, "Lucien"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Evaluation not found for the given author and id")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}