package com.example.projetotestesjunit5.business;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.projetotestesjunit5.infrastructure.PessoaRepository;
import com.example.projetotestesjunit5.infrastructure.entity.Pessoa;
import com.example.projetotestesjunit5.infrastructure.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
public class PessoaServiceTest {

    @InjectMocks
    PessoaService service;

    @Mock
    PessoaRepository repository;

    Pessoa pessoa;

    @BeforeEach
    public void setUp() {
        pessoa = new Pessoa(
                "André",
                "12312312312",
                "Desenvolvedor Java",
                45,
                "Praia Grande",
                "Av. Lincoln, 806",
                806);
    }

    @Test
    void deveBuscarPEssoasPorCPFComSucesso() {
        // cenário

        when(repository.findPessoa(pessoa.getCpf())).thenReturn(List.of(pessoa));

        // execução

        List<Pessoa> pessoas = service.buscaPessoasPorCpf(pessoa.getCpf());

        // verificação
        assertEquals(List.of(pessoa), pessoas);
        verify(repository).findPessoa(pessoa.getCpf());
        verifyNoMoreInteractions(repository);

    }

    @Test
    void naoDeveChamarORepositoryCasoParametroCPFNulo() {
        // execução

        final BusinessException e = assertThrows(BusinessException.class,
                () -> {
                    service.buscaPessoasPorCpf(null);
                });

        // verificação
        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Erro ao buscar pessoas por cpf = null"));
        assertThat(e.getCause(), notNullValue());
        assertThat(e.getCause().getMessage(), is("Cpf é obrigatório!"));
        verifyNoInteractions(repository);

    }

    @Test
    void deveAcionarExceptionQuandoRepositoryFalhar() {

        // cenário

        when(repository.findPessoa(pessoa.getCpf())).thenThrow(new RuntimeException("Falha ao buscar pessoas por cpf"));
        // execução
        final BusinessException e = assertThrows(BusinessException.class, () -> {
            service.buscaPessoasPorCpf(pessoa.getCpf());
        });

        // verificação
        assertThat(e.getMessage(), is(format("Erro ao buscar pessoas por cpf = %s", pessoa.getCpf())));
        assertThat(e.getCause().getClass(), is(RuntimeException.class));
        assertThat(e.getCause().getMessage(), is("Falha ao buscar pessoas por cpf"));
        verify(repository).findPessoa(pessoa.getCpf());
        verifyNoMoreInteractions(repository);
    }

}
