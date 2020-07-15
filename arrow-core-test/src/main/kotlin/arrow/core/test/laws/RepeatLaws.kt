package arrow.core.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Repeat
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object RepeatLaws {
  fun <F> laws(
    RP: Repeat<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    ZipLaws.laws(RP, GENK, EQK) + repeatLaws(RP, GENK, EQK)

  fun <F> laws(
    RP: Repeat<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ) = ZipLaws.laws(RP, GENK, EQK, FOLD) + repeatLaws(RP, GENK, EQK)

  private fun <F> repeatLaws(
    RP: Repeat<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    listOf(
      Law("RepeatLaws: zip with RHS repeat is neutral to the LHS") {
        RP.zipWithRhsRepeatIsNeutralToTheLhs(GENK.genK(Arb.int()), EQK.liftEq(Int.eq()))
      },
      Law("RepeatLaws: zip with LHS repeat is neutral to the RHS") {
        RP.zipWithLhsRepeatIsNeutralToTheRhs(GENK.genK(Arb.int()), EQK.liftEq(Int.eq()))
      }
    )

  fun <F, A> Repeat<F>.zipWithRhsRepeatIsNeutralToTheLhs(G: Arb<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G) { a: Kind<F, A> ->
      a.zip(repeat("foo")).map { it.a }.equalUnderTheLaw(a, EQ)
    }

  fun <F, A> Repeat<F>.zipWithLhsRepeatIsNeutralToTheRhs(G: Arb<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(G) { a: Kind<F, A> ->
      repeat("foo").zip(a).map { it.b }.equalUnderTheLaw(a, EQ)
    }
}
