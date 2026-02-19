package br.com.busco.viagem.app;

import br.com.busco.viagem.app.cmd.CriarPresenca;
import br.com.busco.viagem.app.cmd.EmbarcarAluno;
import br.com.busco.viagem.app.cmd.RegistrarAusencia;
import br.com.busco.viagem.app.cmd.RegistrarFalta;
import br.com.busco.viagem.domain.Presenca;
import br.com.busco.viagem.domain.PresencaRepository;
import br.com.busco.viagem.sk.ids.AlunoId;
import br.com.busco.viagem.sk.ids.PresencaId;
import br.com.busco.viagem.sk.ids.ViagemId;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaServiceIntegrationTest {

    @Mock
    private PresencaRepository repository;

    @Mock
    private BuscarAlunoPorCarterinhaGateway carterinhaGateway;

    @InjectMocks
    private PresencaService presencaService;

    @Captor
    private ArgumentCaptor<Presenca> presencaCaptor;

    private ViagemId viagemId;
    private AlunoId alunoId;
    private PresencaId presencaId;
    private Presenca presenca;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        viagemId = ViagemId.randomId();
        alunoId = AlunoId.randomId();
        presencaId = PresencaId.randomId();
        now = LocalDateTime.now();

        presenca = mock(Presenca.class);
        lenient().when(presenca.getId()).thenReturn(presencaId);
    }

    @Nested
    @DisplayName("Tests for CriarPresenca command")
    class CriarPresencaTests {

        @Test
        @DisplayName("Should create presence successfully")
        void shouldCreatePresenceSuccessfully() {
            // Arrange
            CriarPresenca cmd = CriarPresenca.builder()
                    .aluno(alunoId)
                    .id(viagemId)
                    .build();

            Presenca novaPresenca = Presenca.of(alunoId, viagemId);
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act
            PresencaId result = presencaService.handle(cmd);

            // Assert
            assertThat(result).isEqualTo(presencaId);
            verify(repository).save(presencaCaptor.capture());
            Presenca capturedPresenca = presencaCaptor.getValue();
            assertThat(capturedPresenca.getAluno()).isEqualTo(alunoId);
            assertThat(capturedPresenca.getViagem()).isEqualTo(viagemId);
        }

        @Test
        @DisplayName("Should throw exception when creating presence with null aluno")
        void shouldThrowExceptionWhenAlunoIsNull() {
            // Arrange
            CriarPresenca cmd = CriarPresenca.builder()
                    .aluno(null)
                    .id(viagemId)
                    .build();

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw exception when creating presence with null viagem")
        void shouldThrowExceptionWhenViagemIsNull() {
            // Arrange
            CriarPresenca cmd = CriarPresenca.builder()
                    .aluno(alunoId)
                    .id(null)
                    .build();

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Tests for EmbarcarAluno command")
    class EmbarcarAlunoTests {

        @Test
        @DisplayName("Should register presence successfully")
        void shouldRegisterPresenceSuccessfully() {
            // Arrange
            String carterinha = "CAR123";
            EmbarcarAluno cmd = EmbarcarAluno.builder()
                    .id(viagemId)
                    .aluno(carterinha)
                    .build();

            when(carterinhaGateway.buscarAlunoPorCarterinha(carterinha)).thenReturn(alunoId);
            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.of(presenca));
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act
            PresencaId result = presencaService.handle(cmd);

            // Assert
            assertThat(result).isEqualTo(presencaId);
            verify(presenca).registrarPresenca();
            verify(repository).save(presenca);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when presence not found")
        void shouldThrowEntityNotFoundExceptionWhenPresenceNotFound() {
            // Arrange
            String carterinha = "CAR123";
            EmbarcarAluno cmd = EmbarcarAluno.builder()
                    .id(viagemId)
                    .aluno(carterinha)
                    .build();

            when(carterinhaGateway.buscarAlunoPorCarterinha(carterinha)).thenReturn(alunoId);
            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Not found any Account with code.");
        }

        @Test
        @DisplayName("Should throw exception when gateway returns null aluno")
        void shouldThrowExceptionWhenGatewayReturnsNull() {
            // Arrange
            String carterinha = "CAR123";
            EmbarcarAluno cmd = EmbarcarAluno.builder()
                    .id(viagemId)
                    .aluno(carterinha)
                    .build();

            when(carterinhaGateway.buscarAlunoPorCarterinha(carterinha)).thenReturn(null);

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw exception when viagem is null")
        void shouldThrowExceptionWhenViagemIsNull() {
            // Arrange
            EmbarcarAluno cmd = EmbarcarAluno.builder()
                    .id(null)
                    .aluno("CAR123")
                    .build();

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Tests for RegistrarAusencia command")
    class RegistrarAusenciaTests {

        @Test
        @DisplayName("Should register absence with justification successfully")
        void shouldRegisterAbsenceWithJustificationSuccessfully() {
            // Arrange
            String motivo = "Student was sick";
            RegistrarAusencia cmd = RegistrarAusencia.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .motivo(motivo)
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.of(presenca));
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act
            PresencaId result = presencaService.handle(cmd);

            // Assert
            assertThat(result).isEqualTo(presencaId);
            verify(presenca).justificarFalta(motivo);
            verify(repository).save(presenca);
        }

        @Test
        @DisplayName("Should register absence with empty justification")
        void shouldRegisterAbsenceWithEmptyJustification() {
            // Arrange
            RegistrarAusencia cmd = RegistrarAusencia.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .motivo("")
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.of(presenca));
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act
            PresencaId result = presencaService.handle(cmd);

            // Assert
            assertThat(result).isEqualTo(presencaId);
            verify(presenca).justificarFalta("");
        }

        @Test
        @DisplayName("Should register absence with null justification")
        void shouldRegisterAbsenceWithNullJustification() {
            // Arrange
            RegistrarAusencia cmd = RegistrarAusencia.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .motivo(null)
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.of(presenca));
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act
            PresencaId result = presencaService.handle(cmd);

            // Assert
            assertThat(result).isEqualTo(presencaId);
            verify(presenca).justificarFalta(null);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when presence not found for absence")
        void shouldThrowEntityNotFoundExceptionWhenPresenceNotFoundForAbsence() {
            // Arrange
            RegistrarAusencia cmd = RegistrarAusencia.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .motivo("Sick")
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Not found any Account with code.");
        }
    }

    @Nested
    @DisplayName("Tests for RegistrarFalta command")
    class RegistrarFaltaTests {

        @Test
        @DisplayName("Should register absence without justification successfully")
        void shouldRegisterAbsenceWithoutJustificationSuccessfully() {
            // Arrange
            RegistrarFalta cmd = RegistrarFalta.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.of(presenca));
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act
            PresencaId result = presencaService.handle(cmd);

            // Assert
            assertThat(result).isEqualTo(presencaId);
            verify(presenca).registrarFalta();
            verify(repository).save(presenca);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when presence not found for absence without justification")
        void shouldThrowEntityNotFoundExceptionWhenPresenceNotFoundForAbsenceWithoutJustification() {
            // Arrange
            RegistrarFalta cmd = RegistrarFalta.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Not found any Account with code.");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Concurrency Tests")
    class EdgeCasesAndConcurrencyTests {

        @Test
        @DisplayName("Should handle duplicate registration attempts")
        void shouldHandleDuplicateRegistrationAttempts() {
            // Arrange
            String carterinha = "CAR123";
            EmbarcarAluno cmd = EmbarcarAluno.builder()
                    .id(viagemId)
                    .aluno(carterinha)
                    .build();

            when(carterinhaGateway.buscarAlunoPorCarterinha(carterinha)).thenReturn(alunoId);

            // Simulate presence already marked
            when(repository.findByAlunoAndViagem(alunoId, viagemId))
                    .thenReturn(Optional.of(presenca));

            doThrow(new IllegalStateException("Presence already registered"))
                    .when(presenca).registrarPresenca();

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Presence already registered");
        }

        @Test
        @DisplayName("Should handle multiple status changes")
        void shouldHandleMultipleStatusChanges() {
            // Arrange
            RegistrarFalta faltaCmd = RegistrarFalta.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .build();

            RegistrarAusencia ausenciaCmd = RegistrarAusencia.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .motivo("Medical appointment")
                    .build();

            when(repository.findByAlunoAndViagem(alunoId, viagemId))
                    .thenReturn(Optional.of(presenca));
            when(repository.save(any(Presenca.class))).thenReturn(presenca);

            // Act - First register absence
            presencaService.handle(ausenciaCmd);

            // Then try to register presence (should fail based on business rules)
            doThrow(new IllegalStateException("Cannot change status after absence is registered"))
                    .when(presenca).registrarPresenca();

            // Assert
            assertThatThrownBy(() -> presencaService.handle(faltaCmd))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("Should handle transaction rollback on repository failure")
        void shouldHandleTransactionRollbackOnRepositoryFailure() {
            // Arrange
            CriarPresenca cmd = CriarPresenca.builder()
                    .aluno(alunoId)
                    .id(viagemId)
                    .build();

            when(repository.save(any(Presenca.class)))
                    .thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThatThrownBy(() -> presencaService.handle(cmd))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Database error");
        }

        @Test
        @DisplayName("Should handle multiple operations on same presence")
        void shouldHandleMultipleOperationsOnSamePresence() {
            // Arrange
            CriarPresenca criarCmd = CriarPresenca.builder()
                    .aluno(alunoId)
                    .id(viagemId)
                    .build();

            String carterinha = "CAR123";
            EmbarcarAluno embarcarCmd = EmbarcarAluno.builder()
                    .id(viagemId)
                    .aluno(carterinha)
                    .build();

            RegistrarFalta faltaCmd = RegistrarFalta.builder()
                    .id(viagemId)
                    .aluno(alunoId)
                    .build();

            // Simulate presence that can be created, then marked present, then absent
            Presenca novaPresenca = mock(Presenca.class);
            when(novaPresenca.getId()).thenReturn(presencaId);

            when(repository.save(any(Presenca.class))).thenReturn(novaPresenca);
            when(carterinhaGateway.buscarAlunoPorCarterinha(carterinha)).thenReturn(alunoId);

            // After creation, the presence exists
            when(repository.findByAlunoAndViagem(alunoId, viagemId))
                    .thenReturn(Optional.of(novaPresenca));

            // Act - Create
            PresencaId createResult = presencaService.handle(criarCmd);
            assertThat(createResult).isEqualTo(presencaId);

            // Act - Embark
            PresencaId embarkResult = presencaService.handle(embarcarCmd);
            assertThat(embarkResult).isEqualTo(presencaId);
            verify(novaPresenca).registrarPresenca();

            // Act - Register absence
            PresencaId faltaResult = presencaService.handle(faltaCmd);
            assertThat(faltaResult).isEqualTo(presencaId);
            verify(novaPresenca).registrarFalta();
        }
    }
}