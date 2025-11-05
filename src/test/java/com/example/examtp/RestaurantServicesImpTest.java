package com.example.examtp;

import com.example.examtp.dto.restaurant.create.CreateRestaurantDto;
import com.example.examtp.dto.restaurant.create.CreateRestaurantMapper;
import com.example.examtp.dto.restaurant.read.RestaurantDto;
import com.example.examtp.dto.restaurant.read.RestaurantMapper;
import com.example.examtp.dto.restaurant.update.UpdateRestaurantDto;
import com.example.examtp.entities.Restaurant;
import com.example.examtp.exceptions.AppException;
import com.example.examtp.repositories.RestaurantRepository;
import com.example.examtp.services.restaurant.RestaurantServicesImp;
import com.example.examtp.services.uploadS3.S3UploadService;
import org.junit.jupiter.api.BeforeEach;
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
class RestaurantServicesImpTest {

    @Mock private RestaurantRepository restaurantRepository;
    @Mock private RestaurantMapper restaurantMapper;
    @Mock private CreateRestaurantMapper createRestaurantMapper;
    @Mock private S3UploadService uploadService;

    @InjectMocks private RestaurantServicesImp service;

    @Captor private ArgumentCaptor<Restaurant> restaurantCaptor;

    private Restaurant restaurant;
    private RestaurantDto restaurantDto;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Auberge Test");
        restaurant.setAddress("1 rue des tests, Paris");

        restaurantDto = mock(RestaurantDto.class);
    }

    @Test
    @DisplayName("getAllRestaurants - retourne la liste mappée de DTO")
    void getAllRestaurants_returnsDtos() {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));
        when(restaurantMapper.toDto(restaurant)).thenReturn(restaurantDto);

        List<RestaurantDto> result = service.getAllRestaurants();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isSameAs(restaurantDto);
        verify(restaurantRepository).findAll();
        verify(restaurantMapper).toDto(restaurant);
    }

    @Test
    @DisplayName("getRestaurantById - restaurant trouvé")
    void getRestaurantById_found() {
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toDto(restaurant)).thenReturn(restaurantDto);

        RestaurantDto result = service.getRestaurantById(1L);

        assertThat(result).isSameAs(restaurantDto);
        verify(restaurantRepository).findById(1L);
        verify(restaurantMapper).toDto(restaurant);
    }

    @Test
    @DisplayName("getRestaurantById - restaurant introuvable -> AppException 404")
    void getRestaurantById_notFound() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRestaurantById(99L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Restaurant not found")
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("createRestaurant - avec image -> upload S3 puis save")
    void createRestaurant_withImage() {
        CreateRestaurantDto dto = mock(CreateRestaurantDto.class);
        MultipartFile file = mock(MultipartFile.class);
        when(dto.restaurantImage()).thenReturn(file);

        Restaurant toSave = new Restaurant();
        when(createRestaurantMapper.toEntity(dto)).thenReturn(toSave);
        when(uploadService.uploadRestaurantImage(file)).thenReturn("https://minio.local/restaurant/1.png");
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> {
            Restaurant r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });
        when(restaurantMapper.toDto(any(Restaurant.class))).thenReturn(restaurantDto);

        RestaurantDto result = service.createRestaurant(dto);

        assertThat(result).isSameAs(restaurantDto);
        verify(uploadService).uploadRestaurantImage(file);
        verify(restaurantRepository).save(restaurantCaptor.capture());
        assertThat(restaurantCaptor.getValue().getRestaurantImageUrl()).isEqualTo("https://minio.local/restaurant/1.png");
    }

    @Test
    @DisplayName("createRestaurant - sans image -> save direct sans upload")
    void createRestaurant_withoutImage() {
        CreateRestaurantDto dto = mock(CreateRestaurantDto.class);
        when(dto.restaurantImage()).thenReturn(null);

        Restaurant toSave = new Restaurant();
        when(createRestaurantMapper.toEntity(dto)).thenReturn(toSave);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(toSave);
        when(restaurantMapper.toDto(toSave)).thenReturn(restaurantDto);

        RestaurantDto result = service.createRestaurant(dto);

        assertThat(result).isSameAs(restaurantDto);
        verify(uploadService, never()).uploadRestaurantImage(any());
        verify(restaurantRepository).save(toSave);
    }

    @Test
    @DisplayName("updateRestaurant - met à jour nom et adresse")
    void updateRestaurant_updatesFields() {
        UpdateRestaurantDto updateDto = mock(UpdateRestaurantDto.class);
        when(updateDto.name()).thenReturn("Nouveau Nom");
        when(updateDto.address()).thenReturn("Nouvelle Adresse");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        when(restaurantMapper.toDto(restaurant)).thenReturn(restaurantDto);

        RestaurantDto result = service.updateRestaurant(1L, updateDto);

        assertThat(result).isSameAs(restaurantDto);
        assertThat(restaurant.getName()).isEqualTo("Nouveau Nom");
        assertThat(restaurant.getAddress()).isEqualTo("Nouvelle Adresse");
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    @DisplayName("calculateAverageRating - sans évaluations -> -1")
    void calculateAverage_noEvaluations() {
        restaurant.setEvaluations(List.of());
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        double avg = service.calculateAverageRating(1L);

        assertThat(avg).isEqualTo(-1.0);
    }

    @Test
    @DisplayName("calculateAverageRating - avec évaluations -> moyenne correcte")
    void calculateAverage_withEvaluations() {
        com.example.examtp.entities.Evaluation e1 = new com.example.examtp.entities.Evaluation();
        e1.setNote(2);
        com.example.examtp.entities.Evaluation e2 = new com.example.examtp.entities.Evaluation();
        e2.setNote(3);
        restaurant.setEvaluations(List.of(e1, e2));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(restaurant));

        double avg = service.calculateAverageRating(1L);

        assertThat(avg).isEqualTo(2.5);
    }
}