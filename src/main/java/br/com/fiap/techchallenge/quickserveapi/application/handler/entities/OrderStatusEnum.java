package br.com.fiap.techchallenge.quickserveapi.application.handler.entities;

public enum OrderStatusEnum {
        RECEBIDO,
        EM_PREPARACAO,
        PRONTO,
        FINALIZADO;

        // Método para verificar se um valor é válido
        public static boolean isValid(String value) {
                for (OrderStatusEnum status : values()) {
                        if (status.name().equalsIgnoreCase(value)) {
                                return true;
                        }
                }
                return false;
        }
}
