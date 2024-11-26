# language: pt

Funcionalidade: Pedido

    Cenario: Registrar Pedido
        Quando registar um novo pedido
        Então o pedido é registrado com sucesso
        E deve ser apresentado

    Cenario: Buscar Pedido
        Dado que um pedido ja foi publicado
        Quando efetuar a busca pelo pedido
        Então o pedido é exibido com sucesso

    Cenario: Buscar Status de pagamento do pedido
        Dado que um pedido ja foi publicado
        Quando efetuar a busca pelo pedido
        Então o status do pagamento é exibido com sucesso

    Cenario: Alterar Status do pedido
        Dado que um pedido ja foi publicado
        Quando efetuar requisição para alterar status do pedido
        Então o status do pedido é atualizado com sucesso
        E deve ser apresentado

    Cenario: Alterar Status do pagamento do pedido
        Dado que um pedido ja foi publicado
        Quando efetuar requisição para alterar status do pagamento do pedido
        Então o status do pagamento do pedido é atualizado com sucesso
        E deve ser apresentado
