package ru.fizteh.fivt.students.dzvonarev.filemap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ServletGet extends HttpServlet {
    private TransactionManager manager;

    public ServletGet(TransactionManager transactionManager) {
        manager = transactionManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String transactionId = request.getParameter("tid");
        if (transactionId == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "transaction id not found");
            return;
        }
        if (!Transaction.isValid(transactionId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid transaction id");
        }
        String key = request.getParameter("key");
        if (key == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "key is not found");
            return;
        }
        Transaction transaction = manager.getTransaction(transactionId);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "no transaction is found");
            return;
        }
        try { // run transaction
            String value = transaction.get(key);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF8");
            response.getWriter().println(value);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

}
