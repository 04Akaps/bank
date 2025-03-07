package org.example.domains.history.repository

import org.example.common.exception.CustomException
import org.example.common.exception.ErrorCode
import org.example.common.global.Global
import org.example.config.MongoTableCollector
import org.example.types.dto.History
import org.example.types.entity.HistoryDoc
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Sort


@Repository
class HistoryRepositoryCustom(
    private val template: HashMap<String, MongoTemplate>,
    private val historyUserRepository: HistoryUserRepository,
    private val global: Global
) {

    fun findLatestTransactionHistory(ulid : String, limit : Int = 30) : List<History> {
        val criteria = Criteria().orOperator(
            Criteria.where("fromUlid").`is`(ulid),
            Criteria.where("toUlid").`is`(ulid)
        )

        val query = Query(criteria)
            .with(Sort.by(Sort.Direction.DESC, "time"))
            .limit(limit)

        query.fields().exclude("_id")


        val result : List<HistoryDoc> = template(MongoTableCollector.Bank).find(query, HistoryDoc::class.java)

        return result.map { doc ->
            val fromUser =  getUserName(doc.fromUlid)
            val toUser = getUserName(doc.toUlid)
            doc.toHistory(fromUser, toUser)
        }
    }

    private fun getUserName(ulid : String) :String {
        var userName = global.getUsernameByUlid(ulid)

        if (userName.isEmpty()) {
            historyUserRepository.findByUlid(ulid)?.let {
                global.addUserMapping(ulid, it.username)
                userName = it.username
            } ?: run {
                global.addUserMapping(ulid, "")
            }
        }

        return userName
    }

    private fun template(c: MongoTableCollector) : MongoTemplate {
        val table = template[c.table]

        table?.let { return it }

        throw CustomException(ErrorCode.FailedToFindTemplate)
    }
}
